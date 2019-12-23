package com.freenow.query.search;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.EngineType;
import com.freenow.domainvalue.OnlineStatus;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public class GenericRsqlSpecification<T> implements Specification<T>
{

    private static final long serialVersionUID = -3106575348566100071L;

    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;


    public GenericRsqlSpecification(final String property, final ComparisonOperator operator, final List<String> arguments)
    {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }


    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder)
    {
        final List<Object> args = castArguments(root);
        final Object argument = args.get(0);
        switch (RsqlSearchOperation.getSimpleOperator(operator))
        {

            case EQUAL:
            {
                if (argument instanceof String)
                {
                    return builder.like(this.<String>getPath(root), argument.toString().replace('*', '%'));
                }
                else if (argument == null)
                {
                    return builder.isNull(getPath(root));
                }
                else
                {
                    return builder.equal(getPath(root), argument);
                }
            }
            case NOT_EQUAL:
            {
                if (argument instanceof String)
                {
                    return builder.notLike(this.<String>getPath(root), argument.toString().replace('*', '%'));
                }
                else if (argument == null)
                {
                    return builder.isNotNull(getPath(root));
                }
                else
                {
                    return builder.notEqual(getPath(root), argument);
                }
            }
            case GREATER_THAN:
            {
                return builder.greaterThan(this.<String>getPath(root), argument.toString());
            }
            case GREATER_THAN_OR_EQUAL:
            {
                return builder.greaterThanOrEqualTo(this.<String>getPath(root), argument.toString());
            }
            case LESS_THAN:
            {
                return builder.lessThan(this.<String>getPath(root), argument.toString());
            }
            case LESS_THAN_OR_EQUAL:
            {
                return builder.lessThanOrEqualTo(this.<String>getPath(root), argument.toString());
            }
            case IN:
                return getPath(root).in(args);
            case NOT_IN:
                return builder.not(getPath(root).in(args));
        }

        return null;
    }


    private List<Object> castArguments(final Root<T> root)
    {

        final Class<? extends Object> type = getPath(root).getJavaType();

        final List<Object> args = arguments.stream().map(arg -> {
            if (type.equals(Integer.class))
            {
                return Integer.parseInt(arg);
            }
            else if (type.equals(Long.class))
            {
                return Long.parseLong(arg);
            }
            else if (type.equals(OnlineStatus.class))
            {
                return OnlineStatus.valueOf(arg);
            }
            else if (type.equals(EngineType.class))
            {
                return EngineType.valueOf(arg);
            }
            else
            {
                return arg;
            }
        }).collect(Collectors.toList());

        return args;
    }


    //Making this generic requires ugly hacks
    private boolean isFieldInCar(String fieldName)
    {
        boolean result = true;
        try
        {
            CarDO.class.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException ex)
        {
            result = false;
        }
        return result;
    }


    private <K> Path<K> getPath(Root<T> root)
    {
        if (isFieldInCar(property))
        {
            return root.<K>get("car").get(property);
        }
        return root.<K>get(property);
    }
}

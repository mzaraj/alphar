package pl.com.tt.alpha.filters;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import pl.com.tt.alpha.common.StringUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.nonNull;
import static pl.com.tt.alpha.filters.SafeEscapedLikeBuilder.MatchMode.ANYWHERE;
import static pl.com.tt.alpha.filters.SafeEscapedLikeBuilder.MatchMode.START;
import static pl.com.tt.alpha.filters.SafeEscapedLikeBuilder.safeLike;
import static pl.com.tt.alpha.filters.SafeEscapedLikeBuilder.safeLikeIgnoreCase;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationBuilder {

    private static SpecificationBuilder INSTANCE = new SpecificationBuilder();

    public static synchronized SpecificationBuilder getInstance() {
        return INSTANCE;
    }

    public Specification getSpecification(Filterable filter) {
        log.trace("SpecificationBuilder.getSpecification() {}", filter);
        Specification specifications = Specification.where(alwaysTrue());

        Class<? extends Filterable> clazz = filter.getClass();
        List<Field> fields = getFields(Lists.newArrayList(), clazz);
        for (Field field : fields) {
            FilterBy filterBy = field.getAnnotation(FilterBy.class);
            if (nonNull(filterBy)) {
                Specification specification = handleAnnotation(filter, field, filterBy);
                if (nonNull(specification)) {
                    specifications = specifications.and(specification);
                }
            }
        }
        return specifications;
    }

    private List<Field> getFields(List<Field> fields, Class<?> clazz) {
        fields.addAll(Lists.newArrayList(clazz.getDeclaredFields()));
        if (nonNull(clazz.getSuperclass())) {
            getFields(fields, clazz.getSuperclass());
        }
        return fields;
    }

    private Specification handleAnnotation(Filterable filter, Field field, FilterBy filterBy) {
        try {
            String fieldName = filterBy.fieldName();
            log.trace("Handle annotation FilterBy on field {}", fieldName);
            field.setAccessible(true);
            Object value = field.get(filter);
            if (nonNull(value)) {
                if (StringUtils.hasValue(value)) {
                    return null;
                }
                return createSpecification(filterBy.condition(), value, fieldName);
            }
        } catch (IllegalAccessException e) {
            log.error("Error during getting value from field " + field.getName(), e);
        }
        return null;
    }

    private Specification createSpecification(ConditionType condition, Object value, String fieldName) {
        log.trace("Create specification for condition {}, field {} and value {}", condition, fieldName, value);
        if (ConditionType.EQUAL.equals(condition)) {
            return equal(value, fieldName);
        }
        if (ConditionType.GREATER_THAN_OR_EQUAL.equals(condition)) {
            return greaterThanOrEqualTo(value, fieldName);
        }
        if (ConditionType.LESS_THAN_OR_EQUAL.equals(condition)) {
            return lessThanOrEqualTo(value, fieldName);
        }
        if (ConditionType.IN.equals(condition)) {
            return in(value, fieldName);
        }
        if (ConditionType.LIKE.equals(condition)) {
            return like(value, fieldName);
        }
        if (ConditionType.LIKE_IGNORE_CASE.equals(condition)) {
            return likePart(value, fieldName);
        }
        if (ConditionType.LIKE_DATE.equals(condition)) {
            return likeDate(value, fieldName);
        }
        if (ConditionType.LIKE_FLOATING_POINT.equals(condition)) {
            return likeFloatingPoint(value, fieldName);
        }
        if (ConditionType.LIKE_TIME.equals(condition)) {
            return likeTime(value, fieldName);
        }
        throw new IllegalStateException("Unsupported condition type " + condition);
    }

    private Specification alwaysTrue() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("id"));
    }

    private Specification equal(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(getRoot(root, entityFieldName), searchValue);
    }

    private Specification greaterThanOrEqualTo(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(entityFieldName), (Comparable) searchValue);
    }

    private Specification lessThanOrEqualTo(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(entityFieldName), (Comparable) searchValue);
    }

    private Specification in(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> getRoot(root, entityFieldName).in((Collection<?>) searchValue);
    }

    private Specification like(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> safeLike(criteriaBuilder, getRoot(root, entityFieldName).as(String.class), String.valueOf(searchValue),
            START);
    }

    private Specification likePart(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> safeLikeIgnoreCase(criteriaBuilder, getRoot(root, entityFieldName).as(String.class),
            String.valueOf(searchValue), ANYWHERE);
    }

    private Specification likeDate(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> safeLike(criteriaBuilder, convertToDate(criteriaBuilder, getRoot(root, entityFieldName)),
            String.valueOf(searchValue), START);
    }

    private Specification likeTime(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> safeLike(criteriaBuilder, convertToTime(criteriaBuilder, getRoot(root, entityFieldName)),
            String.valueOf(searchValue).replace(".", ","), START);
    }

    private Specification likeFloatingPoint(Object searchValue, String entityFieldName) {
        return (root, criteriaQuery, criteriaBuilder) -> safeLike(criteriaBuilder, getRoot(root, entityFieldName).as(String.class),
            StringUtils.changeFloatValueForDatabase(String.valueOf(searchValue)), START);
    }

    private Expression<String> convertToDate(CriteriaBuilder cb, Expression<String> value) {
        return cb.function("to_char", String.class, value, cb.literal("dd/MM/yy HH24:mi:ss"));
    }

    private Expression<String> convertToTime(CriteriaBuilder cb, Expression<String> value) {
        return cb.function("to_char", String.class, value, cb.literal("HH24:mi:ssxFF"));
    }

    private Expression<String> getRoot(Root root, String entityFieldName) {
        if (entityFieldName.contains(".")) {
            int i = 0;
            String[] split = entityFieldName.split("\\.");
            Join join = root.join(split[i++], JoinType.LEFT);
            for (; i < split.length - 1; i++) {
                join = join.join(split[i]);
            }
            return join.get(split[i]);
        }
        return root.get(entityFieldName);
    }
}

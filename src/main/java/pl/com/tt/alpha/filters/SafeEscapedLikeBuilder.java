package pl.com.tt.alpha.filters;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class SafeEscapedLikeBuilder {
    private SafeEscapedLikeBuilder() {
    }

    public static Predicate safeLike(CriteriaBuilder cb, Expression<String> property, String value, MatchMode matchMode) {
        value = escapeSpecialSqlChars(value);
        value = matchPart(value, matchMode);
        return cb.like(property, value, '!');
    }

    public static Predicate safeLike(CriteriaBuilder cb, Expression<String> property, String value) {
        value = escapeSpecialSqlChars(value);
        return cb.like(property, value, '!');
    }

    public static Predicate safeLikeIgnoreCase(CriteriaBuilder cb, Expression<String> property, String value, MatchMode matchMode) {
        value = escapeSpecialSqlChars(value);
        value = matchPart(value, matchMode);
        return cb.like(cb.lower(property), value.toLowerCase(), '!');
    }

    public static Predicate safeLikeIgnoreCase(CriteriaBuilder cb, Expression<String> property, String value) {
        value = escapeSpecialSqlChars(value);
        return cb.like(cb.lower(property), value.toLowerCase(), '!');
    }

    private static String matchPart(String value, MatchMode matchMode) {
        switch (matchMode) {
            case EXACT:
                return value;
            case START:
                return value + "%";
            case END:
                return "%" + value;
            case ANYWHERE:
                return "%" + value + "%";
            default:
                return value;
        }
    }

    private static String escapeSpecialSqlChars(String value) {
        return value
            .replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_");
    }

    public enum MatchMode {
        ANYWHERE, END, START, EXACT
    }
}

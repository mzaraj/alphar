package pl.com.tt.alpha.events.vm;

import lombok.Data;
import pl.com.tt.alpha.common.ViewModel;
import pl.com.tt.alpha.filters.ConditionType;
import pl.com.tt.alpha.filters.FilterBy;
import pl.com.tt.alpha.filters.Filterable;

import java.time.Instant;

@Data
public class EventFilteredVm implements Filterable, ViewModel {

    @FilterBy(fieldName = "name", condition = ConditionType.LIKE_IGNORE_CASE)
    private String name;

    @FilterBy(fieldName = "city", condition = ConditionType.LIKE_IGNORE_CASE)
    private String city;

    @FilterBy(fieldName = "category.id", condition = ConditionType.LIKE_IGNORE_CASE)
    private Long categoryId;

    @FilterBy(fieldName = "date", condition = ConditionType.LIKE_IGNORE_CASE)
    private Instant date;
}

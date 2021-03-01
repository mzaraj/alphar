package pl.com.tt.alpha.category.vm.request;

import lombok.Data;
import pl.com.tt.alpha.common.ViewModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryVm implements ViewModel {

    private Long id;

    @Size(max = 30)
    @NotBlank
    private String name;

}

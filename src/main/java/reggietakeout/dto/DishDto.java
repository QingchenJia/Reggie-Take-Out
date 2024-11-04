package reggietakeout.dto;

import lombok.Data;
import reggietakeout.entity.Dish;
import reggietakeout.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
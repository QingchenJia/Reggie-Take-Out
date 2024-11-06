package reggietakeout.dto;

import lombok.Data;
import reggietakeout.entity.Setmeal;
import reggietakeout.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}

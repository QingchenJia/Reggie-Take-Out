package reggietakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggietakeout.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

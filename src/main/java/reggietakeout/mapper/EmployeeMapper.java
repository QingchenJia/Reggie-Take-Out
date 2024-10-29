package reggietakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggietakeout.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

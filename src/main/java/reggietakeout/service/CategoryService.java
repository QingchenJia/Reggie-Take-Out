package reggietakeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void insert(Category category);

    Category selectByName(String name);

    Page selectPage(Page pageInfo);

    void deleteById(Long id);

    List<Category> selectByType(Integer type);
}

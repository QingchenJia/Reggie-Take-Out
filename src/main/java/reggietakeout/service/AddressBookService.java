package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void setDefault(Long id,Long userId);
}

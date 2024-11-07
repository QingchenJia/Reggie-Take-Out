package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.AddressBook;
import reggietakeout.mapper.AddressBookMapper;
import reggietakeout.service.AddressBookService;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    /**
     * 设置默认地址
     * 当用户选择一个地址作为默认地址时，该方法会将用户的所有其他地址的默认状态取消，然后将选定的地址设置为默认地址
     *
     * @param id 地址ID，表示用户选定的地址
     * @param userId 用户ID，表示地址所属的用户
     */
    @Override
    public void setDefault(Long id, Long userId) {
        // 创建一个Lambda更新包装器，用于构建更新条件和设置字段值
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件：根据用户ID找到所有地址，并将这些地址的默认状态设置为0（非默认）
        updateWrapper.eq(AddressBook::getUserId, userId)
                .set(AddressBook::getIsDefault, 0);

        // 执行更新操作，取消所有地址的默认状态
        update(updateWrapper);

        // 创建一个新的地址簿对象，用于更新选定的地址的默认状态
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        addressBook.setIsDefault(1);

        // 根据地址ID更新地址的默认状态为1（默认）
        updateById(addressBook);
    }
}

package reggietakeout.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CaptchaUtils {
    public static final List<Integer> NUMBERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    /**
     * 生成验证码
     * <p>
     * 生成一个4位数字的验证码字符串此方法使用随机数来选择4个数字，
     * 将它们拼接成一个字符串，用作验证码生成的验证码仅包含数字，
     * 长度固定为4位
     *
     * @return 生成的验证码字符串
     */
    public static String generateCaptcha() {
        // 创建一个随机数生成器
        Random random = new Random();
        // 使用StringBuilder来拼接验证码字符
        StringBuilder code = new StringBuilder();

        // 循环4次，生成4位验证码
        for (int i = 0; i < 4; i++) {
            // 在0到9之间随机生成一个整数
            Integer index = random.nextInt(10);
            // 将随机选出的数字添加到验证码字符串中
            code.append(NUMBERS.get(index));
        }

        // 将StringBuilder对象转换为字符串，作为验证码返回
        return code.toString();
    }
}

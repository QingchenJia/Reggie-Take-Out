package reggietakeout.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的ID
     * <p>
     * 使用ThreadLocal来存储每个线程独有的ID，确保在多线程环境下每个线程都有独立的ID副本，不会相互干扰
     * 这种方法常用于在跨多个方法调用中保持数据的唯一性，特别是在Web服务中跟踪单个请求
     *
     * @param id 需要设置的当前线程ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前线程的唯一标识符
     * <p>
     * 此方法用于获取存储在ThreadLocal中的当前线程的唯一ID
     * ThreadLocal是一个线程封闭的数据存储类，每个线程都有自己的ThreadLocal变量副本，
     * 这意味着每个线程都可以独立地修改自己的副本，而不会影响其他线程
     *
     * @return 当前线程的唯一ID
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}

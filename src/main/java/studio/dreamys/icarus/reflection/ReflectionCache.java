package studio.dreamys.icarus.reflection;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
public class ReflectionCache {
    private Class<?> iPage;
    private Map<Class<?>, Field[]> iGroupMap;

    public ReflectionCache(Class<?> iPage, Map<Class<?>, Field[]> iGroupMap) {
        this.iPage = iPage;
        this.iGroupMap = iGroupMap;
    }
}

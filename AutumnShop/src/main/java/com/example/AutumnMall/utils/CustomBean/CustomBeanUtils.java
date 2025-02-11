package com.example.AutumnMall.utils.CustomBean;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

@Component
public class CustomBeanUtils {

    /**
     * source 객체의 null이 아닌 프로퍼티를 destination 객체로 복사
     * @param source 복사할 원본 객체
     * @param destination 복사할 대상 객체
     * @return 업데이트된 destination 객체
     */
    public <T, U> U copyProperties(T source, U destination) {
        // source 또는 destination이 null일 경우 null을 반환
        if (source == null || destination == null) {
            return null;
        }

        // BeanWrapper를 사용하여 source와 destination의 프로퍼티에 접근
        final BeanWrapper src = new BeanWrapperImpl(source);
        final BeanWrapper dest = new BeanWrapperImpl(destination);

        // source 객체의 모든 필드를 순회하며 복사
        for (Field property : source.getClass().getDeclaredFields()) {
            // 필드에 접근할 수 있도록 설정 (private, protected 등 포함)
            property.setAccessible(true);

            // source 필드의 값을 가져옴
            Object sourceProperty = src.getPropertyValue(property.getName());

            // source의 값이 null이 아니고, 컬렉션 타입이 아닌 경우에만 복사
            if (sourceProperty != null && !(sourceProperty instanceof Collection<?>)) {
                // destination의 필드 타입과 일치하는지 체크
                if (dest.isWritableProperty(property.getName())) {
                    dest.setPropertyValue(property.getName(), sourceProperty);
                }
            }
        }

        return destination;
    }
}

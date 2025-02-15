package com.example.AutumnMall.config;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public TransactionInterceptor customTransactionInterceptor() {
        TransactionInterceptor interceptor = new TransactionInterceptor();

        // HashMap으로 트랜잭션 속성 설정
        Map<String, String> txAttributes = new HashMap<>();

        // 📌 공통 트랜잭션 규칙
        txAttributes.put("execution(* com.example.AutumnMall..service.*.add*(..))", "PROPAGATION_REQUIRED");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.update*(..))", "PROPAGATION_REQUIRED");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.delete*(..))", "PROPAGATION_REQUIRED");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.minus*(..))", "PROPAGATION_REQUIRED");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.expire*(..))", "PROPAGATION_REQUIRED");

        txAttributes.put("execution(* com.example.AutumnMall..service.*.get*(..))", "PROPAGATION_SUPPORTS");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.find*(..))", "PROPAGATION_SUPPORTS");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.is*(..))", "PROPAGATION_SUPPORTS");
        txAttributes.put("execution(* com.example.AutumnMall..service.*.purchased*(..))", "PROPAGATION_SUPPORTS");

        // 📌 개별 설정이 필요한 경우 추가 ( 예시 )
        txAttributes.put("execution(* com.example.AutumnMall.Payment.service.PaymentService.get*(..))", "PROPAGATION_SUPPORTS");
        txAttributes.put("execution(* com.example.AutumnMall.Payment.service.PaymentService.purchased*(..))", "PROPAGATION_SUPPORTS");

        // HashMap -> Properties 변환
        Properties transactionProperties = new Properties();
        transactionProperties.putAll(txAttributes);

        interceptor.setTransactionAttributes(transactionProperties);
        return interceptor;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor(TransactionInterceptor customTransactionInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        // 트랜잭션 전파 속성 설정
        pointcut.setExpression(
                "execution(* com.example.AutumnMall.Product.service.*.*(..)) || " +
                        "execution(* com.example.AutumnMall.Cart.service.*.*(..)) || " +
                        "execution(* com.example.AutumnMall.Member.service.*.*(..)) || " +
                        "execution(* com.example.AutumnMall.Payment.service.*.*(..))"
        );

        return new DefaultPointcutAdvisor(pointcut, customTransactionInterceptor);
    }
}


// 1. PROPAGATION_REQUIRED (기본값) : 트랜잭션이 이미 존재하면 그 트랜잭션을 사용하고, 존재하지 않으면 새로운 트랜잭션을 시작합니다.
// 예: 주문이 이루어지는 메서드. 주문이 완료되기 전에 여러 작업이 트랜잭션 내에서 수행되어야 하므로, 반드시 트랜잭션을 사용해야 합니다.
// transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED,readOnly=false");

// 2. PROPAGATION_SUPPORTS : 기존 트랜잭션이 존재하면 해당 트랜잭션을 사용하고, 없으면 트랜잭션을 생성하지 않습니다.
// 예: 상품 정보 조회 메서드. 상품을 조회하는 메서드는 트랜잭션을 사용하지 않아도 되지만, 트랜잭션 내에서 실행될 수도 있습니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.get*(..))", "PROPAGATION_SUPPORTS,readOnly=true");

// 3. PROPAGATION_REQUIRES_NEW : 항상 새로운 트랜잭션을 생성합니다. 기존 트랜잭션이 있으면 잠시 일시 중단됩니다.
// 예: 리뷰 등록 메서드. 리뷰를 추가하는 작업은 주문 처리와는 독립적이어야 하므로 새로운 트랜잭션을 사용합니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.add*(..))", "PROPAGATION_REQUIRES_NEW,readOnly=false");

// 4. PROPAGATION_MANDATORY : 반드시 기존 트랜잭션이 존재해야 합니다. 없으면 예외가 발생합니다.
// 예: 상품 수정 메서드. 상품 정보는 이미 진행 중인 트랜잭션 내에서 처리되어야 하므로, 기존 트랜잭션이 있어야 합니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.update*(..))", "PROPAGATION_MANDATORY,readOnly=false");

// 5. PROPAGATION_NESTED : 기존 트랜잭션 내에서 중첩 트랜잭션을 생성합니다. 중첩된 트랜잭션은 독립적으로 롤백될 수 있습니다.
// 예: 상품 평점 업데이트 메서드. 기존 트랜잭션 내에서 특정 작업을 롤백할 수 있도록 중첩된 트랜잭션을 사용할 수 있습니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.nested*(..))", "PROPAGATION_NESTED,readOnly=false");

// 6. PROPAGATION_NOT_SUPPORTED : 트랜잭션을 사용하지 않고 실행됩니다.
// 예: 백그라운드에서 진행되는 메서드. 예를 들어, 로그를 저장하는 작업은 트랜잭션 내에서 실행할 필요가 없으므로 이 전파 옵션을 사용합니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.nonTransactional*(..))", "PROPAGATION_NOT_SUPPORTED,readOnly=false");

// 7. PROPAGATION_NEVER : 트랜잭션이 없어야만 실행됩니다. 트랜잭션 내에서 실행되면 예외가 발생합니다.
// 예: 특정 보안 작업을 수행하는 메서드. 트랜잭션 내에서 실행되면 안 되는 작업입니다.
// transactionAttributes.setProperty("execution(* com.example.AutumnMall.Product.service.*.never*(..))", "PROPAGATION_NEVER,readOnly=false");

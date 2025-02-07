package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Favorites;
import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Member.repository.FavoritesRepository;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addFavorites(Long memberId, Long productId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));
        if(!favoritesRepository.existsByMemberAndProduct(member, product)){
            Favorites favorites = new Favorites();
            favorites.setMember(member);
            favorites.setProduct(product);
            favoritesRepository.save(favorites);

            // 로그 추가: 즐겨찾기 추가된 경우
            log.info("멤버 {}가 제품 {}을(를) 즐겨찾기에 추가했습니다.", memberId, productId);
        }else{
            // 로그 추가: 이미 즐겨찾기에 있는 경우
            log.info("멤버 {}는 제품 {}을(를) 이미 즐겨찾기 목록에 추가했습니다.", memberId, productId);
        }
    }

    @Transactional
    public void removeFavorites(Long memberId, Long productId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));
        favoritesRepository.deleteByMemberAndProduct(member, product);

        // 로그 추가: 즐겨찾기 삭제된 경우
        log.info("멤버 {}가 제품 {}을(를) 즐겨찾기 목록에서 삭제했습니다.", memberId, productId);
    }

    @Transactional(readOnly = true)
    public List<Product> getFavoritesProductIdByMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        List<Favorites> favorites = favoritesRepository.findByMember(member);

        return favorites.stream()
                .map(Favorites::getProduct)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isProductInFavorites(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));

        // 사용자의 즐겨찾기 목록에 해당 제품이 있는지 확인
        return favoritesRepository.existsByMemberAndProduct(member, product);
    }
}

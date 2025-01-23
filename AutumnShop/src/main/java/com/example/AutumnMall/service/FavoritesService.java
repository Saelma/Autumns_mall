package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Favorites;
import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Product;
import com.example.AutumnMall.repository.FavoritesRepository;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        }

    }

    @Transactional
    public void removeFavorites(Long memberId, Long productId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));
        favoritesRepository.deleteByMemberAndProduct(member, product);
    }

    @Transactional(readOnly = true)
    public List<Long> getFavoritesProductIdByMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        List<Favorites> favorites = favoritesRepository.findByMember(member);

        List<Long> productIds = new ArrayList<>();
        for(Favorites favorite : favorites){
            productIds.add(favorite.getProduct().getId());
        }
        return productIds;
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

package com.example.AutumnMall.Cart.mapper;

import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CartItemMapper {
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true) // CartItems는 별도로 처리
    public abstract CartItem addCartItemDtoToCartItem(AddCartItemDto addCartItemDto);
}

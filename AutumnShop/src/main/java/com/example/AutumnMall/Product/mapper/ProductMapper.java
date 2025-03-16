    package com.example.AutumnMall.Product.mapper;

    import com.example.AutumnMall.Product.domain.Product;
    import com.example.AutumnMall.Product.dto.AddProductDto;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    @Mapper(componentModel = "spring")
    public interface ProductMapper {

        @Mapping(target = "category", ignore = true)
        @Mapping(source = "price", target = "price")
        @Mapping(source = "description", target = "description")
        @Mapping(target = "imageUrl", ignore = true)
        @Mapping(source = "title", target = "title")
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "rating.rate", ignore = true)
        @Mapping(target = "rating.count", ignore = true)
        @Mapping(target = "favorites", ignore = true)
        @Mapping(target = "recentProducts", ignore = true)
        Product addProductDtoToProduct(AddProductDto addProductDto);
    }

package com.example.products.mappers;

import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.models.ProductModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toDto (ProductModel productModel);
    ProductModel toModel (ProductCreateDto productCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto (ProductUpdateDto productUpdateDto, @MappingTarget ProductModel productModel );
}

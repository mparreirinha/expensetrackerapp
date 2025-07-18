package com.parreirinha.expensetrackerapp.transactions.mapper;

import com.parreirinha.expensetrackerapp.category.mapper.CategoryMapper;
import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = CategoryMapper.class, componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Transaction toTransaction(TransactionRequestDto dto);

    TransactionResponseDto toTransactionResponseDto(Transaction transaction);

    List<TransactionResponseDto> toTransactionResponseDtoList(List<Transaction> transactions);

}

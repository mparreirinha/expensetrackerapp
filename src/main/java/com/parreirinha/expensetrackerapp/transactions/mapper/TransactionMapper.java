package com.parreirinha.expensetrackerapp.transactions.mapper;

import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    Transaction toTransaction(TransactionRequestDto dto);

    TransactionResponseDto toTransactionResponseDto(Transaction transaction);

    List<TransactionResponseDto> toTransactionResponseDtoList(List<Transaction> transactions);

}

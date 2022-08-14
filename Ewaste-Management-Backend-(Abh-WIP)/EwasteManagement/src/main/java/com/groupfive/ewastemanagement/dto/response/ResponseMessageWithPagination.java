package com.groupfive.ewastemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessageWithPagination {
    String status;
    int pageSize;
    int pageNo;
    int totalRecords;
    Object data;
}

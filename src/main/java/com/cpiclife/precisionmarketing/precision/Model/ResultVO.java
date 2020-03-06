package com.cpiclife.precisionmarketing.precision.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.logging.Logger;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO {
    private String fields;
    private long amount;
    private long usedAmount;
    private long selected;
    private String groupGuestName;
}

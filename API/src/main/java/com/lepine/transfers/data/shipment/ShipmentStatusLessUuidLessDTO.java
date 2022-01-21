package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShipmentStatusLessUuidLessDTO {
    private List<TransferUuidLessDTO> transfers;
    private LocalDate expectedDate;
    private String orderNumber;
}

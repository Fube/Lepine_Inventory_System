package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.validation.DaysFromNow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShipmentStatusLessUuidLessDTO {
    private List<TransferUuidLessDTO> transfers;

    @DaysFromNow(days = 3, message = "{shipment.expected.date.too.early}")
    @NotNull
    private LocalDate expectedDate;

    private String orderNumber;
}

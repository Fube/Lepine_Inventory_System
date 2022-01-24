package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.validation.DaysFromNow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShipmentStatusLessCreatedByLessUuidLessDTO {

    @Valid
    @Size(min = 1, message = "{shipment.transfers.size.min}")
    @NotNull(message = "{shipment.transfers.not_null}")
    private List<TransferUuidLessDTO> transfers;

    @DaysFromNow(days = 3, message = "{shipment.expected.date.too.early}")
    @NotNull
    private LocalDate expectedDate;

    @NotNull(message = "{shipment.order.number.not_null}")
    private String orderNumber;

    @NotNull(message = "{shipment.to.not_null}")
    private UUID to;
}

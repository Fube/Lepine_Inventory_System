package com.lepine.transfers.controllers.shipment;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.data.shipment.ShipmentStatusLessCreatedByLessUuidLessDTO;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.exceptions.auth.DefaultLoginNotAllowedException;
import com.lepine.transfers.services.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonPatch;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/shipments")
public class ShipmentController {

    private final static UUID DEFAULT_LOGIN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    @GetMapping
    public Page<Shipment> findAll(@AuthenticationPrincipal User user,

                                  @RequestParam(required = false, name = "confirmed") Optional<Boolean> isConfirmed,
                                  @RequestParam(required = false, name = "from") Optional<String> from,
                                  @RequestParam(required = false, name = "to") Optional<String> to,

                                  @RequestParam(required = false, defaultValue = "1")
                                  @Min(value = 1, message = "{pagination.page.min}") final int page,

                                  @RequestParam(required = false, defaultValue = "10")
                                  @Min(value = 1, message = "{pagination.size.min}") final int size) {

        if(isConfirmed.isPresent() && isConfirmed.get()) {
            return OneIndexedPageAdapter.of(findAllFullyConfirmed(from, to, PageRequest.of(page - 1, size)));
        } else if(from.isPresent() || to.isPresent()) {
            log.info("Trying to use from and / or to without confirmed flag being set or being set to false");
            throw new RuntimeException("No");
        }

        final PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("expectedDate").descending());

        log.info("Fetching all shipments for user {} with page request {}", user.getUsername(), pageRequest);

        final String roleName = user.getRole().getName();
        if(roleName.equals("MANAGER")) {
            log.info("User is a manager, fetching all shipments");
            return shipmentService.findAll(pageRequest);
        } else if(roleName.equals("CLERK")) {
            log.info("User is a clerk, fetching all accepted shipments");
            return shipmentService.findAllAccepted(pageRequest);
        }

        log.info("User is a salesperson, fetching only relevant shipments");

        return shipmentService.findAllByUserUuid(user.getUuid(), pageRequest);
    }

    public Page<Shipment> findAllFullyConfirmed(
            final Optional<String> from,
            final Optional<String> to,
            final PageRequest pageRequest
    ){
        log.info("Fetching all fully confirmed shipments with page request {} and time range {} - {}", pageRequest, from, to);
        if(from.isPresent() && to.isPresent()) {
            return OneIndexedPageAdapter.of(shipmentService.findAllFullyConfirmed(
                    ZonedDateTime.parse(from.get()),
                    ZonedDateTime.parse(to.get()),
                    pageRequest)
            );
        }

        return OneIndexedPageAdapter.of(shipmentService.findAllFullyConfirmed(pageRequest));
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public Shipment create(@AuthenticationPrincipal User user,

                           @RequestBody
                           @Valid ShipmentStatusLessCreatedByLessUuidLessDTO shipmentStatusLessCreatedByLessUuidLessDTO
    ) {
        log.info("Creating shipment for user {}", user.getUsername());

        log.info("Checking for default login");
        if(user.getUuid().equals(DEFAULT_LOGIN_UUID)) {
            log.info("Default login detected, denying creation");
            throw new DefaultLoginNotAllowedException();
        }

        log.info("Mapping shipment status less created by less uuid less DTO to shipment");
        final ShipmentStatusLessUuidLessDTO mapped =
            shipmentMapper.toStatusLessUuidLessDTO(shipmentStatusLessCreatedByLessUuidLessDTO, user);
        log.info("Mapped shipment status less uuid less DTO to shipment");

        return shipmentService.create(mapped);
    }

    @PatchMapping(
            path = "/{uuid}",
            consumes = "application/json-patch+json",
            produces = "application/json"
    )
    public Shipment update(
            @PathVariable UUID uuid,
            @RequestBody JsonPatch jsonPatch) {
        log.info("Updating shipment with uuid {}", uuid);
        return shipmentService.update(uuid, jsonPatch);
    }
}

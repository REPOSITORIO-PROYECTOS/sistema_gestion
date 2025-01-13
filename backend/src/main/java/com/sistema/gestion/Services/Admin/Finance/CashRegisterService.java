package com.sistema.gestion.Services.Admin.Finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Finance.CashRegister;
import com.sistema.gestion.Repositories.Admin.Finance.CashRegisterRepository;
import com.sistema.gestion.Repositories.Admin.Finance.PaymentRepository;

import reactor.core.publisher.Mono;

@Service
public class CashRegisterService {
    @Autowired
    private final CashRegisterRepository cashRegisterRepo;

    @Autowired
    private final PaymentRepository paymentRepo;

    public CashRegisterService(CashRegisterRepository cashRegisterRepo, PaymentRepository paymentRepo) {
        this.cashRegisterRepo = cashRegisterRepo;
        this.paymentRepo = paymentRepo;
    }

    public Mono<CashRegister> openCashRegister(String user) {
        return cashRegisterRepo.findFirstByIsClosedFalse()
                .hasElement() // Verifica si hay elementos
                .flatMap(hasOpenRegister -> {
                    if (hasOpenRegister) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Ya existe una caja abierta. Cierre la caja actual antes de abrir una nueva."));
                    }
                    // Crear una nueva caja si no hay una abierta
                    CashRegister newCashRegister = new CashRegister();
                    newCashRegister.setStartDate(LocalDateTime.now());
                    newCashRegister.setIsClosed(false);
                    newCashRegister.setCreatedBy(user);
                    return cashRegisterRepo.save(newCashRegister);
                });
    }

    public Mono<CashRegister> closeCashRegister(String user) {
        return cashRegisterRepo.findFirstByIsClosedFalse()
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No existe una caja abierta para cerrar.")))
                .flatMap(cashRegister -> paymentRepo.findAll()
                        .filter(payment -> payment.getLastPaymentDate().isAfter(cashRegister.getStartDate()))
                        .filter(payment -> payment.getLastPaymentDate().isBefore(LocalDateTime.now()))
                        .reduce(BigDecimal.ZERO,
                                (total, payment) -> total.add(BigDecimal.valueOf(payment.getPaidAmount())))
                        .flatMap(totalAmount -> {
                            cashRegister.setEndDate(LocalDateTime.now());
                            cashRegister.setTotalAmount(totalAmount);
                            cashRegister.setIsClosed(true);
                            cashRegister.setClosedBy(user);
                            return cashRegisterRepo.save(cashRegister);
                        }));
    }

    public Mono<CashRegister> getOpenCashRegister() {
        return cashRegisterRepo.findFirstByIsClosedFalse()
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay una caja abierta actualmente.")))
                .flatMap(cashRegister -> paymentRepo.findAll()
                        .filter(payment -> payment.getLastPaymentDate().isAfter(cashRegister.getStartDate()))
                        .filter(payment -> payment.getLastPaymentDate().isBefore(LocalDateTime.now()))
                        .reduce(BigDecimal.ZERO,
                                (total, payment) -> total.add(BigDecimal.valueOf(payment.getPaidAmount())))
                        .map(totalAmount -> {
                            CashRegister provisionalView = new CashRegister();
                            provisionalView.setId(cashRegister.getId());
                            provisionalView.setStartDate(cashRegister.getStartDate());
                            provisionalView.setEndDate(LocalDateTime.now());
                            provisionalView.setTotalAmount(totalAmount);
                            provisionalView.setIsClosed(false); // Se cierra solo en la vista
                            provisionalView.setCreatedBy(cashRegister.getCreatedBy());
                            provisionalView.setClosedBy("SIN CERRAR");
                            return provisionalView;
                        }));
    }

}

package com.crp.warsztat.controller;

import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.repository.ReservationRepository;
import com.crp.warsztat.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReservationViewController {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reservation")
    public String showForm(Model model) {
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        model.addAttribute("reservation", new Reservation());
        return "reservation_form";
    }

    @PostMapping("/reservation")
    public String addReservation(@ModelAttribute Reservation reservation, Model model) {
        reservationRepository.save(reservation);
        model.addAttribute("msg", "Rezerwacja przyjęta!");
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        model.addAttribute("reservation", new Reservation());
        return "reservation_form";
    }
}

package it.uniroma3.siw.festivalcinema.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalcinema.model.Room;
import it.uniroma3.siw.festivalcinema.service.RoomService;
import it.uniroma3.siw.festivalcinema.util.Pagination;
import jakarta.validation.Valid;

@Controller
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/admin/rooms")
    public String list(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("rooms", roomService.findAll(Pagination.of(page)));
        return "admin/rooms/list";
    }

    @GetMapping("/admin/rooms/new")
    public String createForm(Model model) {
        model.addAttribute("room", new Room());
        return "admin/rooms/form";
    }

    @GetMapping("/admin/rooms/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Room> optional = roomService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/admin/rooms";
        }
        model.addAttribute("room", optional.get());
        return "admin/rooms/form";
    }

    @PostMapping("/admin/rooms")
    public String save(@Valid @ModelAttribute("room") Room room, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/rooms/form";
        }
        roomService.save(room);
        return "redirect:/admin/rooms";
    }
}

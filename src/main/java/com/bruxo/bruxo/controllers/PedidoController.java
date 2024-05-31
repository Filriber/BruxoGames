package com.bruxo.bruxo.controllers;

import com.bruxo.bruxo.models.ItemPedido;
import com.bruxo.bruxo.models.Pedido;
import com.bruxo.bruxo.models.StatusUpdateDto;
import com.bruxo.bruxo.service.AtualizarProdutoRepository;
import com.bruxo.bruxo.service.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
    @Autowired
    private AtualizarProdutoRepository repo;

    @GetMapping("/MostraPedidos")
    public String showPedidosList(Model model, HttpSession session) {
        List<Pedido> pedidos = repo.findAll(Sort.by(Sort.Direction.DESC, "dataPedido"));
        model.addAttribute("pedidos", pedidos);
        return "pedidos/MostraPedidos";
    }

    @PutMapping("/updateStatus/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateDto statusUpdate) {
        Optional<Pedido> pedidoOpt = repo.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            String newStatus = statusUpdate.getStatus();
            pedido.setStatus(newStatus);
            repo.save(pedido);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
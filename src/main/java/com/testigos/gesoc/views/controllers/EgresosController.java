package com.testigos.gesoc.views.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.testigos.gesoc.model.domain.egresos.CriterioSeleccion;
import com.testigos.gesoc.model.domain.egresos.DocumentoComercial;
import com.testigos.gesoc.model.domain.egresos.Egreso;
import com.testigos.gesoc.model.domain.egresos.EgresoConPresupuestos;
import com.testigos.gesoc.model.domain.egresos.Item;
import com.testigos.gesoc.model.domain.egresos.Presupuesto;
import com.testigos.gesoc.model.domain.usuarios.Mensaje;
import com.testigos.gesoc.model.domain.usuarios.Usuario;
import com.testigos.gesoc.model.services.CriteriosService;
import com.testigos.gesoc.model.services.DocumentoComercialService;
import com.testigos.gesoc.model.services.EgresoService;
import com.testigos.gesoc.model.services.ItemService;
import com.testigos.gesoc.model.services.MensajeService;
import com.testigos.gesoc.model.services.PresupuestoService;
import com.testigos.gesoc.model.services.ProveedorService;
import com.testigos.gesoc.model.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/egresos*")
public class EgresosController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private EgresoService egresoService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private DocumentoComercialService documentoComercialService;

    @Autowired
    private CriteriosService criteriosService;

    @Autowired
    private PresupuestoService presupuestoService;

    @GetMapping
    public String egresos(Model model, Authentication auth) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        List<Egreso> egresos = egresoService.findEgresosConProveedor(user.getEntidad());
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egresos", egresos);
        return "egresos";
    }

    @GetMapping(path = "/add")
    public String egresosAdd(Model model, Authentication auth) {
        Usuario user = usuarioService.find(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("new_egreso", new Egreso());
        model.addAttribute("proveedores", proveedorService.findAll());
        return "egresos_add";
    }

    @PostMapping(path = "/add")
    public String addItems(Model model, Authentication auth,
            @ModelAttribute Egreso egreso, @RequestParam("proveedor_elegido") String prov) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        egreso.setComprador(user.getEntidad());
        egreso.setVendedor(proveedorService.find(Integer.parseInt(prov)));
        egreso.setFechaOperacion(LocalDate.now());
        egresoService.persist(egreso);
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egreso", egreso);
        model.addAttribute("new_item", new Item());
        return "item_add";
    }

    @PostMapping(path = "/{egreso_id}/item/add")
    public String addSingleItem(Model model, Authentication auth, @ModelAttribute("new_item") Item item,
            @RequestParam(value = "action") String action, @PathVariable("egreso_id") int egreso_id) {
        Usuario user = usuarioService.find(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        Egreso egreso = egresoService.findEgresoGeneral(egreso_id);
        itemService.persist(item, egreso);
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        if (action.equals("continue")) {
            model.addAttribute("egreso", egreso);
            model.addAttribute("new_item", new Item());
            return "item_add";
        } else
            return "egresos_add_result";
    }

    @GetMapping(path = "/doc_comercial/add")
    public String docComercialAdd(Model model, Authentication auth) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        List<Egreso> egresos = egresoService.findEgresosSinDocumentoComercial(user.getEntidad());
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egresos", egresos);
        model.addAttribute("new_doc", new DocumentoComercial());
        return "doc_comercial_add";
    }

    @PostMapping(path = "/doc_comercial/add")
    public String persistDocComercial(Model model, Authentication auth,
            @ModelAttribute("new_doc") DocumentoComercial documentoComercial, @RequestParam("egreso_id") String egr) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        Egreso egreso = egresoService.findEgresoGeneral(Integer.parseInt(egr));
        documentoComercialService.persist(documentoComercial, egreso);
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        return "doc_comercial_add_result";
    }

    @GetMapping(path = "/cp/add")
    public String egresosConPresupuestoAdd(Model model, Authentication auth) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        List<Usuario> usuarios = usuarioService.findAll(user.getEntidad());
        List<CriterioSeleccion> criterios = criteriosService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("proveedores", proveedorService.findAll());
        model.addAttribute("revisores", usuarios);
        model.addAttribute("criterios", criterios);
        model.addAttribute("new_egreso", new EgresoConPresupuestos());
        return "egresos_add_cp";
    }

    @PostMapping(path = "/cp/add")
    public String persistEgresoCP(Model model, Authentication auth,
            @ModelAttribute EgresoConPresupuestos egresoConPresupuestos, @RequestParam("proveedor_elegido") String prov,
            @RequestParam("revisor_elegido") String rev,
            @RequestParam("criterio_elegido") String crit) {

        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);

        egresoConPresupuestos.setRevisor(usuarioService.findConEntidad(rev));
        egresoConPresupuestos.setCriterio(criteriosService.find(Integer.parseInt(crit)));
        egresoConPresupuestos.setComprador(user.getEntidad());
        egresoConPresupuestos.setVendedor(proveedorService.find(Integer.parseInt(prov)));
        egresoConPresupuestos.setFechaOperacion(LocalDate.now());
        egresoService.persist(egresoConPresupuestos);

        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egreso", egresoConPresupuestos);
        model.addAttribute("new_item", new Item());
        return "item_cp_add";
    }

    @PostMapping(path = "/{egreso_id}/cp/add/item")
    public String addItemCP(Model model, Authentication auth, @ModelAttribute("new_item") Item item,
            @RequestParam(value = "action") String action,
            @PathVariable("egreso_id") int egreso_id) {

        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);

        EgresoConPresupuestos egreso = egresoService.findEgresoCP(egreso_id);
        itemService.persist(item, egreso);

        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egreso", egreso);

        if (action.equals("continue")) {
            model.addAttribute("new_item", new Item());
            return "item_cp_add";
        } else {
            model.addAttribute("new_presupuesto", new Presupuesto());
            return "presupuesto_add";
        }
    }

    @PostMapping(path = "/{egreso_id}/cp/add/presupuesto")
    public String addPresupuesto(Model model, Authentication auth,
            @ModelAttribute("new_presupuesto") Presupuesto presupuesto, @PathVariable("egreso_id") int egreso_id) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);

        EgresoConPresupuestos egreso = egresoService.findEgresoCP(egreso_id);
        presupuesto.setEgresoConPresupuestos(egreso);
        presupuestoService.persist(presupuesto);

        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egreso", egreso);
        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("new_item", new Item());

        return "item_presupuesto_add";
    }

    @PostMapping(path = "/{egreso_id}/cp/add/item/presupuesto/{presupuesto_id}")
    public String addItemPresupuesto(Model model, Authentication auth, @ModelAttribute("new_item") Item item,
            @RequestParam(value = "action") String action, @PathVariable("presupuesto_id") String presu, @PathVariable("egreso_id") int egreso_id) {

        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        Presupuesto presupuesto = presupuestoService.find(Integer.parseInt(presu));
        EgresoConPresupuestos egreso = egresoService.findEgresoCP(egreso_id);

        item.setPresupuesto(presupuesto);
        itemService.persist(item);

        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("egreso", egreso);

        if (action.equals("add_item")) {
            model.addAttribute("new_item", new Item());
            model.addAttribute("presupuesto", presupuesto);
            return "item_presupuesto_add";
        } else if (action.equals("add_presupuesto")) {
            model.addAttribute("new_presupuesto", new Presupuesto());
            return "presupuesto_add";
        } else {
            List<Presupuesto> presupuestos = egresoService.findConPresupuestos(egreso.getId())
                    .getTodosLosPresupuestos();
            model.addAttribute("presupuestos", presupuestos);
            return "presupuesto_eleccion";
        }
    }

    @PostMapping(path = "/{egreso_id}/cp/add/result")
    public String addFinalPresupuesto(Model model, Authentication auth,
            @RequestParam("presupuesto_elegido") String presupuesto, @PathVariable("egreso_id") int egreso_id) {
        Usuario user = usuarioService.findConEntidad(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        EgresoConPresupuestos egreso = egresoService.findConPresupuestos(egreso_id);

        egreso.setPresupuestoElegido(egreso.getTodosLosPresupuestos().stream()
                .filter(p -> p.getId() == Integer.parseInt(presupuesto)).findFirst().get());
        egresoService.updatePresupuesto(egreso);

        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        return "egresos_add_result";
    }

    @GetMapping(path = "/presupuestos/{egreso_id}")
    public String getPresupuestos(Model model, Authentication auth, @PathVariable("egreso_id") int egreso) {
        Usuario user = usuarioService.find(auth.getName());
        List<Mensaje> mensajes = mensajeService.getMensajes(user);
        EgresoConPresupuestos eg = egresoService.findConPresupuestos(egreso);
        List<Presupuesto> presupuestos = new ArrayList<>();
        Presupuesto presupuesto = null;
        if (eg != null) {
            presupuestos = eg.getTodosLosPresupuestos();
            presupuesto = eg.getPresupuestoElegido();
        }
        model.addAttribute("user", user);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("presupuestos", presupuestos);
        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("egreso", egreso);
        return "egreso_presupuestos";
    }
}

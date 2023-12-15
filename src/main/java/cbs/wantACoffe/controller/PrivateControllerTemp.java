package cbs.wantACoffe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador básico para pruebas de permisos. 
 * TODO: En producción se quita
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@RestController
@RequestMapping("/api/temp")
public class PrivateControllerTemp {
    
    /**
     * Devuelve un simple string
     * @return
     */
    @GetMapping("hello")
    public ResponseEntity<String> getMessage(){
        return ResponseEntity.ok().body("Has llegado hasta el mensaje!!!");
    }
}

package ac.cr.tarea1.rest.product;

import ac.cr.tarea1.logic.entity.category.CategoryRepository;
import ac.cr.tarea1.logic.entity.http.GlobalResponseHandler;
import ac.cr.tarea1.logic.entity.http.Meta;
import ac.cr.tarea1.logic.entity.product.Product;
import ac.cr.tarea1.logic.entity.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductContoller {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/GetAllProducts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Consulta de Productos exitosa!!",
                productsPage.getContent(), HttpStatus.OK,meta);
    }

    @PostMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> addUser(@RequestBody Product product, HttpServletRequest request) {
        productRepository.save(product);
        return new GlobalResponseHandler().handleResponse("Producto agregado correctamente!!",
                product, HttpStatus.OK, request);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateOrder(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Producto Actualizado",
                    product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Id del producto no encotrado " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> fountProduct = productRepository.findById(productId);
        if(fountProduct.isPresent()) {
            productRepository.deleteById(productId);
            return new GlobalResponseHandler().handleResponse("Producto eliminado correctamente!!",
                    fountProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Id de la orden no encontrada " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

}

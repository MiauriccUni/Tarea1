package ac.cr.tarea1.rest.category;


import ac.cr.tarea1.logic.entity.category.Category;
import ac.cr.tarea1.logic.entity.category.CategoryRepository;
import ac.cr.tarea1.logic.entity.http.GlobalResponseHandler;
import ac.cr.tarea1.logic.entity.http.Meta;

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
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/getAllCategory")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoryPage.getTotalPages());
        meta.setTotalElements(categoryPage.getTotalElements());
        meta.setPageNumber(categoryPage.getNumber() + 1);
        meta.setPageSize(categoryPage.getSize());

        return new GlobalResponseHandler().handleResponse("Consulta de Categorias Exitosa!!",
                categoryPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping("/postCategory/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Categoia Registrada Exitosamente!!",
                category, HttpStatus.OK, request);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateUser(@PathVariable Long categoryId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            categoryRepository.save(category);

            return new GlobalResponseHandler().handleResponse("Categoria actualizada exitosamente!!",
                    category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("ID de la categoria no encontrada" + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long categoryId, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            Optional<Category> category = categoryRepository.findById(foundCategory.get().getId());
            category.get().getProducts().remove(foundCategory.get());
            categoryRepository.deleteById(foundCategory.get().getId());
            return new GlobalResponseHandler().handleResponse("Categoria Eliminada Exitosamente!!",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("ID dela categoria no encontrado " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

}

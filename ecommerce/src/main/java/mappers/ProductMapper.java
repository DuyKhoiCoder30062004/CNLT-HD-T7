package mappers;
import dtos.ProductDTO;
import entities.Product;
import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setDiscount(product.getDiscount());
        dto.setInventoryQuantity(product.getInventoryQuantity());
        dto.setSoldQuantity(product.getSoldQuantity());
        dto.setCategoryName(product.getCategory().getName());
        dto.setImagePaths(product.getImages()
                .stream()
                .map(img -> img.getPath())
                .collect(Collectors.toList()));
        return dto;
    }
}

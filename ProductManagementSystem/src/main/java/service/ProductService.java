package service;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
import entity.Product;
import entity.ProductDetail;
import repository.ProductRepository;
import jakarta.persistence.EntityManager;
import util.JPAUtil;
import java.util.List;

public class ProductService {

    public List<Product> search(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new ProductRepository(em).search(criteria);
        } finally {
            em.close();
        }
    }

    public long count(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new ProductRepository(em).count(criteria);
        } finally {
            em.close();
        }
    }

    public void createProduct(ProductFormDTO dto) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductRepository repo = new ProductRepository(em);

            if (repo.existsBySku(dto.getSku())) {
                throw new Exception("Mã SKU đã tồn tại trong hệ thống.");
            }

            Product product = new Product();
            mapDtoToEntity(dto, product, em);

            ProductDetail detail = new ProductDetail();
            mapDtoToDetailEntity(dto, detail);

            product.setDetail(detail);
            detail.setProduct(product);

            em.persist(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateProduct(ProductFormDTO dto) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductRepository repo = new ProductRepository(em);

            Product product = repo.findByIdWithDetail(dto.getId())
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm."));

            if (!product.getSku().equals(dto.getSku()) && repo.existsBySku(dto.getSku())) {
                throw new Exception("Mã SKU cập nhật đã tồn tại.");
            }

            mapDtoToEntity(dto, product, em);

            if (product.getDetail() == null) {
                ProductDetail detail = new ProductDetail();
                detail.setProduct(product);
                product.setDetail(detail);
            }
            mapDtoToDetailEntity(dto, product.getDetail());

            em.merge(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteSoftly(Long id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductRepository repo = new ProductRepository(em);
            Product product = repo.findByIdWithDetail(id)
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm."));

            product.setDeleted(true);
            em.merge(product);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void mapDtoToEntity(ProductFormDTO dto, Product product, EntityManager em) throws Exception {
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : true);

        Category cat = em.find(Category.class, dto.getCategoryId());
        if (cat == null || !cat.getStatus()) {
            throw new Exception("Danh mục không hợp lệ hoặc đang bị vô hiệu hoá.");
        }
        product.setCategory(cat);
    }

    private void mapDtoToDetailEntity(ProductFormDTO dto, ProductDetail detail) {
        detail.setManufacturer(dto.getManufacturer());
        detail.setWarrantyMonths(dto.getWarrantyMonths());
        detail.setOrigin(dto.getOrigin());
        detail.setDescription(dto.getDescription());
        detail.setTechnicalSpec(dto.getTechnicalSpec());
    }

    public ProductFormDTO getProductForEdit(Long id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Product p = new ProductRepository(em).findByIdWithDetail(id)
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm."));

            ProductFormDTO dto = new ProductFormDTO();
            dto.setId(p.getId());
            dto.setSku(p.getSku());
            dto.setName(p.getName());
            dto.setPrice(p.getPrice());
            dto.setQuantity(p.getQuantity());
            dto.setStatus(p.getStatus());
            dto.setCategoryId(p.getCategory().getId());

            if (p.getDetail() != null) {
                dto.setManufacturer(p.getDetail().getManufacturer());
                dto.setWarrantyMonths(p.getDetail().getWarrantyMonths());
                dto.setOrigin(p.getDetail().getOrigin());
                dto.setDescription(p.getDetail().getDescription());
                dto.setTechnicalSpec(p.getDetail().getTechnicalSpec());
            }
            return dto;
        } finally {
            em.close();
        }
    }
}
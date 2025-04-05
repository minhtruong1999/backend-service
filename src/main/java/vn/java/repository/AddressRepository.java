package vn.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUserIdAndAddressType(Long userId, Integer addressType);
}

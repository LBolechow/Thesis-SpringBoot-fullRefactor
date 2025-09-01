package pl.lukbol.dyplom.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.lukbol.dyplom.common.Messages;
import pl.lukbol.dyplom.DTOs.response.ApiResponseDTO;
import pl.lukbol.dyplom.DTOs.price.PriceRequestDTO;
import pl.lukbol.dyplom.classes.Price;
import pl.lukbol.dyplom.exceptions.ApplicationException;
import pl.lukbol.dyplom.repositories.PriceRepository;

import java.util.List;
import java.util.Optional;

import static pl.lukbol.dyplom.common.Messages.USER_NOT_FOUND_BY_ID;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;

    }

    public List<Price> getAllPrices() {
        List<Price> prices = priceRepository.findAll();
        return prices;
    }

    @Transactional
    public ApiResponseDTO addPrice(PriceRequestDTO priceRequestDTO) {
        Price newPrice = new Price(priceRequestDTO.item(), priceRequestDTO.price());
        priceRepository.save(newPrice);
        return new ApiResponseDTO(Messages.NEW_PRICE_ADDED_MESSAGE);
    }

    @Transactional
    public ApiResponseDTO deletePrice(Long id) {
        Optional<Price> priceOptional = priceRepository.findById(id);
        priceRepository.delete(priceOptional.get());
        return new ApiResponseDTO(Messages.PRICE_DELETE_MESSAGE);
    }

    @Transactional
    public ApiResponseDTO updatePrice(Long id, PriceRequestDTO priceRequestDTO) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(USER_NOT_FOUND_BY_ID));
        price.setItem(priceRequestDTO.item());
        price.setPrice(priceRequestDTO.price());
        priceRepository.save(price);
        return new ApiResponseDTO(Messages.UPDATE_PRICE_MESSAGE);

    }
}

package com.medicore.application.bed;

import com.medicore.domain.admission.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListBedsUseCase {
    private final BedRepository bedRepository;

    public ListBedsUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Transactional(readOnly = true)
    public List<BedResponse> execute() {
        return bedRepository.findAll().stream().map(BedResponse::from).toList();
    }
}

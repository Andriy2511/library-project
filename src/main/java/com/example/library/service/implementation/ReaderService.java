package com.example.library.service.implementation;

import com.example.library.model.Reader;
import com.example.library.model.Role;
import com.example.library.repository.ReaderRepository;
import com.example.library.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReaderService implements IReaderService {
    ReaderRepository readerRepository;

    @Autowired
    public ReaderService(ReaderRepository readerRepository){
        this.readerRepository = readerRepository;
    }

    @Override
    public void registerUser(Reader reader){
        readerRepository.save(reader);
    }

    @Override
    public boolean isUserExistCheckByUsername(String username){
        return readerRepository.existsByUsername(username);
    }

    @Override
    public boolean isUserExistCheckByEmail(String email){
        return readerRepository.existsByEmail(email);
    }

    @Override
    public Reader findReaderByName(String username){
        return readerRepository.findAllByUsername(username).get();
    }

    @Override
    public Reader findReaderById(Long id){
        return readerRepository.findById(id).orElse(null);
    }

    @Override
    public void saveReader(Reader reader){
        readerRepository.save(reader);
    }

    @Override
    public List<Reader> findAllReadersByRoles(List<Role> roleList) {return readerRepository.findAllByRolesIn(roleList);}

    @Override
    public List<Reader> findAllReadersByRolesWithPagination(List<Role> roleList, int numberOfPage, int recordPerPage){
        Pageable pageable = PageRequest.of(numberOfPage, recordPerPage);
        return readerRepository.findAllByRolesIn(roleList, pageable);
    }

    @Override
    public Long getCountReadersByRole(Role role) {
        return readerRepository.countReadersByRoles(role);
    }
}

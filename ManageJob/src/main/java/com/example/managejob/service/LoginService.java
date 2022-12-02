package com.example.managejob.service;

import com.example.managejob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LoginService implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        com.example.managejob.model.User st = userRepo.findByName(name);

        // Khong ton tai st co name
        if (st == null) {
            throw new UsernameNotFoundException("not found");
        }
        List<SimpleGrantedAuthority> list = new ArrayList<SimpleGrantedAuthority>();

        list.add(new SimpleGrantedAuthority(st.getRole()));
        // tao user cua security // user dang nhap hien tai
        User currentUser = new User(name, st.getPassword(), list);
        System.err.println(name);
        System.err.println(st.getPassword());
        System.err.println(st.getRole());
        System.err.println(currentUser);
// extend User de them thuoc tinh.
        return currentUser;
    }

}

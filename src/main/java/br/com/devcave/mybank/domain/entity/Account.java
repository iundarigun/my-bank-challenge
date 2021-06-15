package br.com.devcave.mybank.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    private Customer owner;

    @ManyToOne(optional = false)
    private Bank bank;

    @Builder.Default
    @OneToMany(mappedBy = "destination", fetch = FetchType.LAZY)
    private List<Transfer> inTransferList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "origin", fetch = FetchType.LAZY)
    private List<Transfer> outTransferList = new ArrayList<>();
}

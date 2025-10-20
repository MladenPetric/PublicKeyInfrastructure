package rs.map.pki.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import java.util.Optional;


@NoArgsConstructor
@Embeddable
public class PkiX500Name {

    @Transient
    private X500Name _dn;

    @Getter
    @Setter
    private String dn;

    public PkiX500Name(X500Name dn) {
        this._dn = dn;
        this.dn = dn.toString();
    }

    public PkiX500Name(String dn) {
        this._dn = new X500Name(dn);
        this.dn = dn;
    }

    public X500Name toX500Name() {
        return _dn;
    }

    @Transient
    public String getOrganization() {
        return getRDN(BCStyle.O);
    }

    @Transient
    public String getCN() {
        return getRDN(BCStyle.CN);
    }

    private String getRDN(ASN1ObjectIdentifier oid) {
        var rdns = _dn.getRDNs(oid);

        return rdns.length > 0 ?
                Optional.ofNullable(rdns[0].getFirst())
                        .map(AttributeTypeAndValue::getValue)
                        .map(Object::toString)
                        .orElse(null)
                : null;
    }

}
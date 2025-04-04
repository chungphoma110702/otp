package LearnJV.example.demo.Entity;


import LearnJV.example.demo.Entity.Converters.UserStatusConverter;
import LearnJV.example.demo.Enum.USER_STATUS;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Convert(converter = UserStatusConverter.class)
    private USER_STATUS status;

}

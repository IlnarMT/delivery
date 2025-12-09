package libs.ddd;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

@MappedSuperclass
public abstract class Entity<TId extends Comparable<TId>> implements Comparable<Entity<TId>> {

    @Id
    protected TId id;

    protected Entity() {
    }

    protected Entity(TId id) {
        this.id = id;
    }

    public TId getId() {
        return this.id;
    }

    protected boolean isTransient() {
        return id == null || id.equals(defaultValue());
    }

    protected TId defaultValue() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this == obj)
            return true;

        if (!(obj instanceof Entity<?> other))
            return false;

        if (!this.getClass().equals(other.getClass()))
            return false;

        if (this.isTransient() || other.isTransient())
            return false;

        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return (getClass() + (id != null ? id.toString() : "")).hashCode();
    }

    @Override
    public int compareTo(Entity<TId> other) {
        if (other == null)
            return 1;

        if (this == other)
            return 0;

        return this.id.compareTo(other.id);
    }
}
package today.what4dinner.what4dinnerauth.repository;

/**
 * Repository for family data persistence.
 */
public interface FamilyRepository {

    /**
     * Inserts a new family record into the database.
     *
     * @param familyName the display name of the family to be created
     * @return the unique identifier (ID) of the newly inserted family
     */
    String insertFamily(String familyName);
}

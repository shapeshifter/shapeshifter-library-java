package org.lfenergy.shapeshifter.core.service.validation;


/**
 * Interface for Contract Support <br/> <br/> Next to implementing this interface, you also have to implement other interfaces (with other concerns); the full list of the
 * interfaces is :
 *
 * <ul>
 *   <li>CongestionPointSupport</li>
 *   <li>ContractSupport</li>
 *   <li>ParticipantSupport</li>
 *   <li>UftpMessageSupport</li>
 *   <li>UftpValidatorSupport</li>
 * </ul>
 *
 * @see CongestionPointSupport
 * @see ContractSupport
 * @see ParticipantSupport
 * @see UftpMessageSupport
 * @see UftpValidatorSupport
 */
public interface ContractSupport {

  /**
   * Checks whether a given <code>contractID</code> is present
   *
   * @param contractId The contract ID to be checked
   * @return Whether the given contract ID is supported
   */
  boolean isSupportedContractID(String contractId);
}

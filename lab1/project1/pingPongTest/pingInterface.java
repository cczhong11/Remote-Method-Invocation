package pingPongTest;

import rmi.RMIException;


/** Simple interface for an Pingpong test

    <p>
    This interface is used in pingpong test.
 */
public interface pingInterface
{
    /** Tests pingpong

        @return <code>string</code>.
        @throws RMIException If the call cannot be complete due to a network
                             error.
     */
    public String ping(int idNumber)
        throws RMIException;

    
}

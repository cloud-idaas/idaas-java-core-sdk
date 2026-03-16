
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;

public class ObtainTokenSample{

    public static void main(String[] args) {

        // Initialize the factory with configuration
        IDaaSCredentialProviderFactory.init();

        // Get credential provider with scope from config file
        // IDaaSCredentialProvider credentialProvider =
        //         IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get credential provider with scope specified by parameter
        IDaaSCredentialProvider credentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);

        // perform token exchange
        String accessToken = credentialProvider.getBearerToken();

        System.out.println(accessToken);
    }
}
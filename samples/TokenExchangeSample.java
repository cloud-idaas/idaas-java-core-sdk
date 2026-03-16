
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.core.util.OAuth2Constants;

public class TokenExchangeSamples {

    public static void main(String[] args) {

        // Initialize the factory with configuration
        IDaaSCredentialProviderFactory.init();

        // The subject token to exchange
        String subjectToken = "";

        //  Get Token Exchange credential provider with scope from config file
        // IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
        //                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get Token Exchange credential provider with scope specified by parameter
        IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider(scope);

        // perform token exchange
        String accessToken = tokenExchangeCredentialProvider.getIssuedToken(
                subjectToken,
                OAuth2Constants.ACCESS_TOKEN_TYPE,
                OAuth2Constants.ACCESS_TOKEN_TYPE
        );

        System.out.println(accessToken);
    }
}
/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * Commons Clause Restriction
 *
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 *
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 *
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

#ifndef _IGNITE_ODBC_SSL_SSL_LIBRARY
#define _IGNITE_ODBC_SSL_SSL_LIBRARY

#include <openssl/ssl.h>
#include <openssl/conf.h>
#include <openssl/err.h>

#include "ignite/common/concurrent.h"
#include "ignite/common/dynamic_load_os.h"

namespace ignite
{
    namespace odbc
    {
        namespace ssl
        {
            /**
             * Functions collection.
             */
            struct SslFunctions
            {
                void *fpSSLeay_version;
                void *fpSSL_CTX_new;
                void *fpSSL_CTX_free;
                void *fpSSL_CTX_set_verify;
                void *fpSSL_CTX_set_verify_depth;
                void *fpSSL_CTX_load_verify_locations;
                void *fpSSL_CTX_use_certificate_chain_file;
                void *fpSSL_CTX_use_RSAPrivateKey_file;
                void *fpSSL_CTX_set_cipher_list;
                void *fpSSL_CTX_ctrl;
                void *fpSSL_get_verify_result;
                void *fpSSL_library_init;
                void *fpSSL_load_error_strings;
                void *fpSSL_get_peer_certificate;
                void *fpSSL_ctrl;
                void *fpSSLv23_client_method;
                void *fpSSL_set_connect_state;
                void *fpSSL_connect;
                void *fpSSL_get_error;
                void *fpSSL_want;
                void *fpSSL_write;
                void *fpSSL_read;
                void *fpSSL_pending;
                void *fpSSL_get_fd;
                void *fpSSL_free;
                void *fpOPENSSL_config;
                void *fpX509_free;
                void *fpBIO_new_ssl_connect;
                void *fpBIO_free_all;
                void *fpBIO_ctrl;
                void *fpERR_get_error;
                void *fpERR_error_string_n;
                void *fpERR_print_errors_fp;

                void *fpOpenSSL_version;
                void *fpSSL_CTX_set_options;
                void *fpOPENSSL_init_ssl;
                void *fpTLS_client_method;
            };

            /**
             * SSL Gateway abstraction.
             * Used as a factory for secure sockets. Needed for dynamic loading
             * of the SSL libraries.
             */
            class SslGateway
            {
            public:
                /**
                 * Get class instance.
                 * @return SslLibrary instance.
                 */
                static SslGateway& GetInstance();

                /**
                 * Try loading SSL library.
                 * @return @c true if loaded successfully, and false otherwise.
                 */
                bool LoadAll();

                /**
                 * Get functions.
                 * @return Functions structure.
                 */
                SslFunctions& GetFunctions()
                {
                    return functions;
                }

                /**
                 * Check whether the libraries are loaded.
                 * @return @c true if loaded.
                 */
                bool Loaded() const
                {
                    return inited;
                }

                char* SSLeay_version_(int type);

                int OPENSSL_init_ssl_(uint64_t opts, const void* settings);

                long SSL_CTX_set_options_(SSL_CTX* ctx, long options);

                long SSL_CTX_ctrl_(SSL_CTX* ctx, int cmd, long larg, void* parg);

                SSL_CTX* SSL_CTX_new_(const SSL_METHOD* meth);

                void SSL_CTX_free_(SSL_CTX* ctx);

                void SSL_CTX_set_verify_(SSL_CTX* ctx, int mode, int (*callback)(int, X509_STORE_CTX*));

                void SSL_CTX_set_verify_depth_(SSL_CTX* ctx, int depth);

                int SSL_CTX_load_verify_locations_(SSL_CTX* ctx, const char* cAfile, const char* cApath);

                int SSL_CTX_use_certificate_chain_file_(SSL_CTX* ctx, const char* file);

                int SSL_CTX_use_RSAPrivateKey_file_(SSL_CTX* ctx, const char* file, int type);

                int SSL_CTX_set_cipher_list_(SSL_CTX* ctx, const char* str);

                long SSL_get_verify_result_(const SSL* s);

                int SSL_library_init_();

                void SSL_load_error_strings_();

                X509* SSL_get_peer_certificate_(const SSL* s);

                long SSL_ctrl_(SSL* s, int cmd, long larg, void* parg);

                long SSL_set_tlsext_host_name_(SSL* s, const char* name);

                void SSL_set_connect_state_(SSL* s);

                int SSL_connect_(SSL* s);

                int SSL_get_error_(const SSL* s, int ret);

                int SSL_want_(const SSL* s);

                int SSL_write_(SSL* s, const void* buf, int num);

                int SSL_read_(SSL* s, void* buf, int num);

                int SSL_pending_(const SSL* ssl);

                int SSL_get_fd_(const SSL* ssl);

                void SSL_free_(SSL* ssl);

                const SSL_METHOD* SSLv23_client_method_();

                const SSL_METHOD* TLS_client_method_();

                void OPENSSL_config_(const char* configName);

                void X509_free_(X509* a);

                BIO* BIO_new_ssl_connect_(SSL_CTX* ctx);

                void BIO_free_all_(BIO* a);

                long BIO_ctrl_(BIO* bp, int cmd, long larg, void* parg);

                long BIO_get_fd_(BIO* bp, int* fd);

                long BIO_get_ssl_(BIO* bp, SSL** ssl);

                long BIO_set_nbio_(BIO* bp, long n);

                long BIO_set_conn_hostname_(BIO* bp, const char* name);

                unsigned long ERR_get_error_();

                void ERR_error_string_n_(unsigned long e, char* buf, size_t len);

                void ERR_print_errors_fp_(FILE *fd);

            private:
                /**
                 * Constructor.
                 */
                SslGateway();

                /**
                 * Destructor.
                 */
                ~SslGateway();

                /**
                 * Unload all SSL symbols.
                 */
                void UnloadAll();

                /**
                 * Load SSL library.
                 * @param name Name.
                 * @return Module.
                 */
                common::dynamic::Module LoadSslLibrary(const char* name);

                /**
                 * Load all SSL libraries.
                 */
                bool LoadSslLibraries();

                /**
                 * Load mandatory SSL methods.
                 *
                 * @throw IgniteError if can not load one of the functions.
                 */
                void LoadMandatoryMethods();

                /**
                 * Try load SSL method.
                 *
                 * @param name Name.
                 * @return Method pointer.
                 */
                void* TryLoadSslMethod(const char* name);

                /**
                 * Load SSL method.
                 * @param name Name.
                 * @return Method pointer.
                 */
                void* LoadSslMethod(const char* name);

                /** Indicates whether the library was inited. */
                bool inited;

                /** Critical section to prevent multiple instance creation. */
                common::concurrent::CriticalSection initCs;

                /** libeay32 module. */
                common::dynamic::Module libeay32;

                /** ssleay32 module. */
                common::dynamic::Module ssleay32;

                /** libcrypto module. */
                common::dynamic::Module libcrypto;

                /** libssl module. */
                common::dynamic::Module libssl;

                /** Functions. */
                SslFunctions functions;
            };
        }
    }
}

#endif //_IGNITE_ODBC_SSL_SSL_LIBRARY
/src
  /app
    /(public)                # public-facing routes
      /                      # home / landing
      /products
        /[slug]              # product details
      /categories
        /[slug]
      /cart
      /checkout
      /orders
        /[id]
      /account               # profile, orders list, addresses
    /(admin)                 # admin-only routes (guarded)
      /admin
        /products
        /orders
        /categories
    /api                     # (optional) edge/serverless proxies if needed
  /features                  # ⬅️ feature-first source
    /catalog
      /components            # ProductCard, ProductGrid, CategoryMenu...
      /hooks                 # useProduct, useSearchProducts...
      /lib                   # catalog API client, mappers
      /types                 # Product, Category TS types
    /cart
      /components            # CartDrawer, CartSummary
      /hooks                 # useCart (Zustand/Redux)
      /lib                   # cart calc, persistence (localStorage)
      /types                 # CartItem, Cart
    /checkout
      /components            # CheckoutForm, AddressForm
      /hooks                 # useCheckout, usePlaceOrder
      /lib                   # payment mock, validators
      /types                 # CheckoutPayload
    /order
      /components            # OrderSummary, OrderList
      /hooks                 # useOrder, useMyOrders
      /lib                   # order API client
      /types
    /user
      /components            # AuthButton, ProfileMenu
      /hooks                 # useCurrentUser, useRequireAuth
      /lib                   # Okta/next-auth client setup
      /types                 # User, Role
    /admin
      /components            # AdminTable, ProductForm
      /hooks                 # useAdminProducts, useAdminOrders
      /lib                   # admin API calls
      /types
  /shared
    /components              # UI kit: Button, Input, Modal, Breadcrumbs
    /icons                   # Lucide wrappers
    /layouts                 # AppShell, AdminLayout
    /providers               # ThemeProvider, QueryClientProvider, AuthProvider
    /lib                     # fetcher, env, formatMoney, zod validators
    /styles                  # globals.css, tailwind.css
    /types                   # common types (Money, Paginated<T>)
  /store                     # Zustand/Redux slices (cart, ui)
  /config                    # runtime config, feature flags
  /tests                     # Playwright/Cypress, React Testing Library setup

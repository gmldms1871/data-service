export interface Product {
  id: number;
  companyId: string;
  categoryId: string | null;
  mainImage: string | null;
  description_image: string | null;
  extension_list: string[] | null;
  description: string | null;
  name: string | null;
  attachment_id: string | null;
  createdAt: string;
  deletedAt: string | null;
  updatedAt: string;
  price: number | null;
  version: number | null;
}
